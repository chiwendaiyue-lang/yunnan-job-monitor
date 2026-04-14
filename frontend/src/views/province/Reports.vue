<template>
  <el-card>
    <template #header>报表审核（省级）</template>
    <el-tabs v-model="tab">
      <el-tab-pane label="待终审" name="p">
        <el-table :data="pending" v-loading="loading">
          <el-table-column prop="enterpriseName" label="企业" />
          <el-table-column prop="surveyPeriodName" label="调查期" />
          <el-table-column prop="status" width="120" />
          <el-table-column label="操作" width="280">
            <template #default="{ row }">
              <el-button type="success" link @click="pApprove(row)">终审通过</el-button>
              <el-button type="warning" link @click="openRet(row)">退回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="全部" name="a">
        <el-table :data="all" v-loading="loading2">
          <el-table-column prop="enterpriseName" label="企业" />
          <el-table-column prop="surveyPeriodName" label="调查期" />
          <el-table-column prop="status" width="140" />
          <el-table-column prop="reportedToNational" label="已报部" width="100" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button v-if="row.status === 'PROVINCE_APPROVED' && !row.reportedToNational" link type="primary" @click="markNat(row)">标记部级上报</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <el-dialog v-model="dlg" title="退回备注" width="520px">
      <el-input v-model="remark" type="textarea" rows="4" />
      <template #footer>
        <el-button @click="dlg=false">取消</el-button>
        <el-button type="primary" @click="confirmRet">退回</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const tab = ref('p')
const pending = ref([])
const all = ref([])
const loading = ref(false)
const loading2 = ref(false)
const dlg = ref(false)
const remark = ref('')
const cur = ref(null)

async function loadP() {
  loading.value = true
  try {
    const { data } = await http.get('/province/reports/pending')
    pending.value = data
  } finally {
    loading.value = false
  }
}

async function loadA() {
  loading2.value = true
  try {
    const { data } = await http.get('/province/reports')
    all.value = data
  } finally {
    loading2.value = false
  }
}

async function pApprove(row) {
  await http.post(`/province/reports/${row.id}/approve`)
  ElMessage.success('已通过')
  await loadP()
  await loadA()
}

function openRet(row) {
  cur.value = row
  remark.value = ''
  dlg.value = true
}

async function confirmRet() {
  await http.post(`/province/reports/${cur.value.id}/return`, { remark: remark.value })
  ElMessage.success('已退回')
  dlg.value = false
  await loadP()
  await loadA()
}

async function markNat(row) {
  await http.post(`/province/reports/${row.id}/mark-national`)
  ElMessage.success('已标记')
  await loadA()
}

watch(tab, (v) => {
  if (v === 'a') loadA()
})

onMounted(async () => {
  await loadP()
})
</script>
