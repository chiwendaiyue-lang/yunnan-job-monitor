<template>
  <el-card>
    <template #header>备案企业</template>
    <el-space style="margin-bottom:12px">
      <el-button type="primary" @click="load">刷新</el-button>
      <el-button @click="exportCsv">导出 CSV</el-button>
      <el-button @click="exportJson">导出 JSON</el-button>
    </el-space>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="name" label="企业名称" />
      <el-table-column prop="orgCode" label="组织机构代码" width="140" />
      <el-table-column prop="regionCity" label="地市" width="100" />
      <el-table-column prop="filingStatus" label="备案状态" width="120" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-if="row.filingStatus === 'SUBMITTED'" type="success" link @click="approve(row)">审核通过</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

async function downloadExport(format, filename) {
  const res = await http.get('/province/export/enterprises', {
    params: { format },
    responseType: 'blob'
  })
  const url = URL.createObjectURL(res.data)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}

const rows = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const { data } = await http.get('/province/enterprises')
    rows.value = data
  } finally {
    loading.value = false
  }
}

async function approve(row) {
  await http.post(`/province/enterprises/${row.id}/approve-filing`)
  ElMessage.success('备案已通过')
  await load()
}

async function exportCsv() {
  await downloadExport('csv', 'enterprises.csv')
}

async function exportJson() {
  await downloadExport('json', 'enterprises.json')
}

onMounted(load)
</script>
