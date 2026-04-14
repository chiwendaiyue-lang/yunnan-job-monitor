<template>
  <el-row :gutter="16">
    <el-col :span="8">
      <el-card shadow="hover">
        <h3>欢迎使用</h3>
        <p>请通过左侧菜单进入各功能模块。企业用户请先完善备案信息，并在开放期内完成就业数据填报。</p>
      </el-card>
    </el-col>
    <el-col :span="8" v-if="auth.isEnterprise">
      <el-card shadow="hover">
        <h3>快捷入口</h3>
        <el-space direction="vertical" alignment="start">
          <el-button type="primary" @click="$router.push('/enterprise/profile')">维护备案</el-button>
          <el-button @click="$router.push('/enterprise/reports')">数据填报</el-button>
        </el-space>
      </el-card>
    </el-col>
    <el-col :span="8" v-if="auth.isCity">
      <el-card shadow="hover">
        <h3>市级审核</h3>
        <el-button type="primary" @click="$router.push('/city/pending')">待审核列表</el-button>
      </el-card>
    </el-col>
    <el-col :span="8" v-if="auth.isProvince">
      <el-card shadow="hover">
        <h3>省级管理</h3>
        <el-space wrap>
          <el-button type="primary" @click="$router.push('/province/reports')">报表审核</el-button>
          <el-button @click="$router.push('/province/enterprises')">备案企业</el-button>
          <el-button @click="$router.push('/analysis')">取样分析</el-button>
        </el-space>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
const auth = useAuthStore()
</script>
