<template>
  <el-card v-loading="loading">
    <template #header>企业备案信息</template>
    <el-alert v-if="data?.filingStatus === 'APPROVED'" type="success" title="备案已通过，信息锁定" show-icon style="margin-bottom:12px" />
    <el-form :model="form" label-width="120px" :disabled="data?.filingStatus === 'APPROVED'">
      <el-row :gutter="12">
        <el-col :span="8"><el-form-item label="所属地市"><el-input v-model="form.regionCity" disabled /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="所属市县"><el-input v-model="form.regionCounty" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="区域"><el-input v-model="form.regionArea" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="12">
        <el-col :span="8"><el-form-item label="组织机构代码"><el-input v-model="form.orgCode" maxlength="9" /></el-form-item></el-col>
        <el-col :span="16"><el-form-item label="企业名称"><el-input v-model="form.name" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="12">
        <el-col :span="8"><el-form-item label="企业性质(一)"><el-input v-model="form.natureLevel1" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="企业性质(二)"><el-input v-model="form.natureLevel2" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="行业(一)"><el-input v-model="form.industryLevel1" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="12">
        <el-col :span="8"><el-form-item label="行业(二)"><el-input v-model="form.industryLevel2" /></el-form-item></el-col>
        <el-col :span="16"><el-form-item label="主要经营业务"><el-input v-model="form.mainBusiness" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="12">
        <el-col :span="8"><el-form-item label="联系人"><el-input v-model="form.contactName" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="地址(省)"><el-input v-model="form.addressLevel1" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="地址(市)"><el-input v-model="form.addressLevel2" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="详细地址"><el-input v-model="form.addressDetail" /></el-form-item>
      <el-row :gutter="12">
        <el-col :span="8"><el-form-item label="邮编"><el-input v-model="form.postalCode" maxlength="6" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="传真"><el-input v-model="form.fax" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="EMAIL"><el-input v-model="form.email" /></el-form-item>
      <el-space>
        <el-button type="primary" :disabled="data?.filingStatus === 'APPROVED'" @click="save">保存</el-button>
        <el-button type="success" :disabled="data?.filingStatus === 'APPROVED'" @click="submit">提交备案</el-button>
      </el-space>
    </el-form>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const loading = ref(false)
const data = ref(null)
const form = reactive({
  regionCity: '', regionCounty: '', regionArea: '', orgCode: '', name: '',
  natureLevel1: '', natureLevel2: '', industryLevel1: '', industryLevel2: '',
  mainBusiness: '', contactName: '', addressLevel1: '', addressLevel2: '', addressDetail: '',
  postalCode: '', phone: '', fax: '', email: ''
})

function apply(d) {
  Object.assign(form, {
    regionCity: d.regionCity,
    regionCounty: d.regionCounty,
    regionArea: d.regionArea || '',
    orgCode: d.orgCode,
    name: d.name,
    natureLevel1: d.natureLevel1,
    natureLevel2: d.natureLevel2,
    industryLevel1: d.industryLevel1,
    industryLevel2: d.industryLevel2,
    mainBusiness: d.mainBusiness,
    contactName: d.contactName,
    addressLevel1: d.addressLevel1,
    addressLevel2: d.addressLevel2,
    addressDetail: d.addressDetail,
    postalCode: d.postalCode,
    phone: d.phone,
    fax: d.fax,
    email: d.email || ''
  })
}

async function load() {
  loading.value = true
  try {
    const { data: d } = await http.get('/enterprise/me')
    data.value = d
    apply(d)
  } finally {
    loading.value = false
  }
}

async function save() {
  try {
    const { data: d } = await http.put('/enterprise/me', { ...form })
    data.value = d
    ElMessage.success('已保存')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '保存失败')
  }
}

async function submit() {
  try {
    const { data: d } = await http.post('/enterprise/me/submit-filing')
    data.value = d
    ElMessage.success('已提交备案审核')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '提交失败')
  }
}

onMounted(load)
</script>
