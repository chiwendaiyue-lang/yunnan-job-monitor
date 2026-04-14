<template>
  <el-card>
    <template #header>通知公告</template>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="createdAt" label="发布时间" width="200" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="open(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dlg" title="通知详情" width="600px">
      <h3>{{ cur?.title }}</h3>
      <p class="muted">{{ cur?.createdAt }}</p>
      <p style="white-space: pre-wrap">{{ cur?.content }}</p>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'

const rows = ref([])
const loading = ref(false)
const dlg = ref(false)
const cur = ref(null)

async function load() {
  loading.value = true
  try {
    const { data } = await http.get('/notifications')
    rows.value = data.map((n) => ({
      ...n,
      createdAt: n.createdAt?.replace('T', ' ').slice(0, 19)
    }))
  } finally {
    loading.value = false
  }
}

function open(row) {
  cur.value = row
  dlg.value = true
}

onMounted(load)
</script>

<style scoped>
.muted { color: #909399; font-size: 13px; }
</style>
