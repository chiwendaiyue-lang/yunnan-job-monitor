<template>
  <el-card>
    <template #header>调查期 / 上报时限</template>
    <el-button type="primary" @click="openCreate" style="margin-bottom:12px">新增调查期</el-button>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="reportStart" label="开始" width="200" />
      <el-table-column prop="reportEnd" label="结束" width="200" />
      <el-table-column prop="active" label="启用" width="80" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="edit(row)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dlg" :title="form.id ? '修改调查期' : '新增调查期'" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.reportStart" type="datetime" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.reportEnd" type="datetime" style="width:100%" /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.active" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const rows = ref([])
const loading = ref(false)
const dlg = ref(false)
const form = reactive({ id: null, name: '', reportStart: null, reportEnd: null, active: true })

async function load() {
  loading.value = true
  try {
    const { data } = await http.get('/survey-periods')
    rows.value = data
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.id = null
  form.name = ''
  const now = new Date()
  form.reportStart = now
  form.reportEnd = new Date(now.getTime() + 30 * 86400000)
  form.active = true
  dlg.value = true
}

function edit(row) {
  form.id = row.id
  form.name = row.name
  form.reportStart = new Date(row.reportStart)
  form.reportEnd = new Date(row.reportEnd)
  form.active = row.active
  dlg.value = true
}

async function save() {
  try {
    const body = {
      name: form.name,
      reportStart: new Date(form.reportStart).toISOString(),
      reportEnd: new Date(form.reportEnd).toISOString(),
      active: form.active
    }
    if (form.id) {
      await http.put(`/province/survey-periods/${form.id}`, body)
    } else {
      await http.post('/province/survey-periods', body)
    }
    ElMessage.success('已保存')
    dlg.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '保存失败')
  }
}

onMounted(load)
</script>
