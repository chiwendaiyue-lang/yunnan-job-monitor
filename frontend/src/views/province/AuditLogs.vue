<template>
  <el-card>
    <template #header>填报日志审计（CC-003）</template>
    <el-table :data="rows" v-loading="loading" height="560">
      <el-table-column prop="createdAt" label="时间" width="200" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="action" label="动作" width="160" />
      <el-table-column prop="entityType" label="实体" width="140" />
      <el-table-column prop="entityId" label="实体ID" width="100" />
      <el-table-column prop="detailJson" label="详情" />
    </el-table>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const rows = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const { data } = await http.get('/province/audit-logs')
    rows.value = data
  } finally {
    loading.value = false
  }
})
</script>
