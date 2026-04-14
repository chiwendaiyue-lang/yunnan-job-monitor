<template>
  <el-card>
    <template #header>通知管理（省）</template>
    <el-button type="primary" @click="openCreate" style="margin-bottom:12px">新增通知</el-button>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="createdAt" label="发布时间" width="200" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="edit(row)">修改</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dlg" :title="form.id ? '修改通知' : '新增通知'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题"><el-input v-model="form.title" maxlength="100" show-word-limit /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" rows="8" maxlength="2000" show-word-limit /></el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/http'

const rows = ref([])
const loading = ref(false)
const dlg = ref(false)
const form = reactive({ id: null, title: '', content: '' })

async function load() {
  loading.value = true
  try {
    const { data } = await http.get('/notifications')
    rows.value = data
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.id = null
  form.title = ''
  form.content = ''
  dlg.value = true
}

function edit(row) {
  form.id = row.id
  form.title = row.title
  form.content = row.content
  dlg.value = true
}

async function save() {
  try {
    if (form.id) {
      await http.put(`/province/notifications/${form.id}`, { title: form.title, content: form.content })
    } else {
      await http.post('/province/notifications', { title: form.title, content: form.content })
    }
    ElMessage.success('已保存')
    dlg.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '失败')
  }
}

async function remove(row) {
  await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' })
  await http.delete(`/province/notifications/${row.id}`)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>
