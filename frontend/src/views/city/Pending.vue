<template>
  <el-card>
    <template #header>待审核企业上报（市级）</template>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="enterpriseName" label="企业" />
      <el-table-column prop="surveyPeriodName" label="调查期" />
      <el-table-column prop="archiveEmployment" label="建档期" width="100" />
      <el-table-column prop="surveyEmployment" label="调查期" width="100" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button type="success" link @click="approve(row)">通过</el-button>
          <el-button type="warning" link @click="ret(row)">退回</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dlg" title="退回备注" width="480px">
      <el-input v-model="remark" type="textarea" rows="4" placeholder="可填写退回理由（CC-001）" />
      <template #footer>
        <el-button @click="dlg=false">取消</el-button>
        <el-button type="primary" @click="confirmReturn">确定退回</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/http'

const rows = ref([])
const loading = ref(false)
const dlg = ref(false)
const remark = ref('')
const cur = ref(null)

async function load() {
  loading.value = true
  try {
    const { data } = await http.get('/city/reports/pending')
    rows.value = data
  } finally {
    loading.value = false
  }
}

async function approve(row) {
  await ElMessageBox.confirm('确认审核通过？', '提示', { type: 'warning' })
  await http.post(`/city/reports/${row.id}/approve`)
  ElMessage.success('已通过')
  await load()
}

function ret(row) {
  cur.value = row
  remark.value = ''
  dlg.value = true
}

async function confirmReturn() {
  await http.post(`/city/reports/${cur.value.id}/return`, { remark: remark.value })
  ElMessage.success('已退回')
  dlg.value = false
  await load()
}

onMounted(load)
</script>
