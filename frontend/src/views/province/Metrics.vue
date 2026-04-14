<template>
  <el-card>
    <template #header>系统监控</template>
    <el-descriptions :column="2" border v-if="m">
      <el-descriptions-item label="堆内存使用(bytes)">{{ m.heapUsedBytes }}</el-descriptions-item>
      <el-descriptions-item label="堆最大(bytes)">{{ m.heapMaxBytes }}</el-descriptions-item>
      <el-descriptions-item label="CPU 核数">{{ m.processors }}</el-descriptions-item>
      <el-descriptions-item label="系统负载">{{ m.systemLoadAverage }}</el-descriptions-item>
      <el-descriptions-item label="磁盘剩余(bytes)">{{ m.diskFreeBytes }}</el-descriptions-item>
      <el-descriptions-item label="磁盘总(bytes)">{{ m.diskTotalBytes }}</el-descriptions-item>
      <el-descriptions-item label="进程运行(ms)">{{ m.appUptimeMs }}</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const m = ref(null)
onMounted(async () => {
  const { data } = await http.get('/province/system/metrics')
  m.value = data
})
</script>
