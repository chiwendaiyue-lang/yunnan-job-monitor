<template>
  <el-card>
    <template #header>用户管理</template>
    <el-button type="primary" @click="dlg=true" style="margin-bottom:12px">新增用户</el-button>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="username" label="用户名" width="160" />
      <el-table-column prop="displayName" label="姓名" />
      <el-table-column label="角色" width="140">
        <template #default="{ row }">{{ row.role?.code }}</template>
      </el-table-column>
      <el-table-column prop="regionCode" label="地区编码" width="120" />
      <el-table-column prop="enterpriseId" label="企业ID" width="100" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="danger" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dlg" title="新增用户" width="520px">
      <el-form :model="f" label-width="100px">
        <el-form-item label="用户名"><el-input v-model="f.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="f.password" type="password" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="f.displayName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="f.roleCode" style="width:100%">
            <el-option label="企业" value="ENTERPRISE" />
            <el-option label="市" value="CITY" />
            <el-option label="省" value="PROVINCE" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区编码"><el-input v-model="f.regionCode" placeholder="市级填530100等" /></el-form-item>
        <el-form-item label="企业ID"><el-input v-model.number="f.enterpriseId" placeholder="企业用户必填" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg=false">取消</el-button>
        <el-button type="primary" @click="create">创建</el-button>
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
const f = reactive({ username: '', password: '', displayName: '', roleCode: 'ENTERPRISE', regionCode: '', enterpriseId: null })

async function load() {
  loading.value = true
  try {
    const { data } = await http.get('/province/users')
    rows.value = data
  } finally {
    loading.value = false
  }
}

async function create() {
  try {
    await http.post('/province/users', {
      username: f.username,
      password: f.password,
      displayName: f.displayName || null,
      roleCode: f.roleCode,
      regionCode: f.regionCode || null,
      enterpriseId: f.enterpriseId || null
    })
    ElMessage.success('已创建')
    dlg.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '失败')
  }
}

async function del(row) {
  await ElMessageBox.confirm('确认删除该用户？', '提示', { type: 'warning' })
  await http.delete(`/province/users/${row.id}`)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>
