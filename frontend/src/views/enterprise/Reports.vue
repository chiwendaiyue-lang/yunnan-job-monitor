<template>
  <el-card>
    <template #header>就业数据填报</template>
    <el-button type="primary" style="margin-bottom:12px" @click="createNew">新建填报</el-button>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="surveyPeriodName" label="调查期" width="160" />
      <el-table-column prop="archiveEmployment" label="建档期就业" width="120" />
      <el-table-column prop="surveyEmployment" label="调查期就业" width="120" />
      <el-table-column prop="status" label="状态" width="140" />
      <el-table-column prop="returnRemark" label="退回备注" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="edit(row)">编辑</el-button>
          <el-button v-if="row.status === 'DRAFT' || row.status === 'RETURNED'" link type="success" @click="submit(row)">提交</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dlg" title="填报" width="720px" @open="onOpenDlg">
      <el-form :model="f" label-width="140px">
        <el-form-item label="调查期">
          <el-select v-model="f.surveyPeriodId" style="width:100%">
            <el-option v-for="p in periods" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="建档期就业人数"><el-input-number v-model="f.archiveEmployment" :min="0" /></el-form-item>
        <el-form-item label="调查期就业人数"><el-input-number v-model="f.surveyEmployment" :min="0" /></el-form-item>
        <el-form-item label="其他原因"><el-input v-model="f.otherReason" type="textarea" rows="2" /></el-form-item>
        <template v-if="f.surveyEmployment < f.archiveEmployment">
          <el-form-item label="减少类型">
            <el-select v-model="f.reduceType" filterable allow-create default-first-option style="width:100%">
              <el-option v-for="x in dict.reduceTypes" :key="x" :label="x" :value="x" />
            </el-select>
          </el-form-item>
          <el-form-item label="主要原因">
            <el-select v-model="f.mainReason" filterable allow-create style="width:100%">
              <el-option v-for="x in dict.mainReasons" :key="x" :label="x" :value="x" />
            </el-select>
          </el-form-item>
          <el-form-item label="主要原因说明"><el-input v-model="f.mainReasonNote" type="textarea" /></el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dlg=false">取消</el-button>
        <el-button type="primary" @click="saveDraft">保存草稿</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const rows = ref([])
const periods = ref([])
const dict = reactive({ reduceTypes: [], mainReasons: [] })
const loading = ref(false)
const dlg = ref(false)
const f = reactive({
  id: null,
  surveyPeriodId: null,
  archiveEmployment: 0,
  surveyEmployment: 0,
  otherReason: '',
  reduceType: '',
  mainReason: '',
  mainReasonNote: '',
  secondaryReason: '',
  secondaryReasonNote: '',
  thirdReason: '',
  thirdReasonNote: ''
})

async function loadDict() {
  const { data } = await http.get('/meta/dictionaries')
  dict.reduceTypes = data.reduceTypes
  dict.mainReasons = data.mainReasons
}

async function loadPeriods() {
  const { data } = await http.get('/survey-periods')
  periods.value = data
}

async function loadRows() {
  loading.value = true
  try {
    const { data } = await http.get('/me/reports')
    rows.value = data
  } finally {
    loading.value = false
  }
}

async function createNew() {
  await loadPeriods()
  f.id = null
  f.surveyPeriodId = periods.value[0]?.id || null
  f.archiveEmployment = 0
  f.surveyEmployment = 0
  f.otherReason = ''
  f.reduceType = ''
  f.mainReason = ''
  f.mainReasonNote = ''
  f.secondaryReason = ''
  f.secondaryReasonNote = ''
  f.thirdReason = ''
  f.thirdReasonNote = ''
  dlg.value = true
}

function edit(row) {
  f.id = row.id
  f.surveyPeriodId = row.surveyPeriodId
  f.archiveEmployment = row.archiveEmployment
  f.surveyEmployment = row.surveyEmployment
  f.otherReason = row.otherReason || ''
  f.reduceType = row.reduceType || ''
  f.mainReason = row.mainReason || ''
  f.mainReasonNote = row.mainReasonNote || ''
  f.secondaryReason = row.secondaryReason || ''
  f.secondaryReasonNote = row.secondaryReasonNote || ''
  f.thirdReason = row.thirdReason || ''
  f.thirdReasonNote = row.thirdReasonNote || ''
  dlg.value = true
}

async function onOpenDlg() {
  if (!periods.value.length) await loadPeriods()
  if (!f.surveyPeriodId && periods.value[0]) f.surveyPeriodId = periods.value[0].id
}

async function saveDraft() {
  try {
    const body = {
      surveyPeriodId: f.surveyPeriodId,
      archiveEmployment: f.archiveEmployment,
      surveyEmployment: f.surveyEmployment,
      otherReason: f.otherReason || ' ',
      reduceType: f.reduceType || null,
      mainReason: f.mainReason || null,
      mainReasonNote: f.mainReasonNote || null,
      secondaryReason: f.secondaryReason || null,
      secondaryReasonNote: f.secondaryReasonNote || null,
      thirdReason: f.thirdReason || null,
      thirdReasonNote: f.thirdReasonNote || null
    }
    const { data } = await http.post('/me/reports', body)
    f.id = data.id
    ElMessage.success('草稿已保存')
    dlg.value = false
    await loadRows()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '保存失败')
  }
}

async function submit(row) {
  try {
    await http.post(`/me/reports/${row.id}/submit`)
    ElMessage.success('已提交审核')
    await loadRows()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '提交失败')
  }
}

onMounted(async () => {
  await loadDict()
  await loadRows()
})
</script>
