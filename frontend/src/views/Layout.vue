<template>
  <el-container class="shell">
    <el-aside width="220px" class="aside">
      <div class="brand">就业失业监测</div>
      <el-menu :default-active="$route.path" router background-color="#001529" text-color="#fff" active-text-color="#ffd04b">
        <el-menu-item index="/dashboard">工作台</el-menu-item>
        <el-menu-item index="/notifications">通知公告</el-menu-item>
        <template v-if="auth.isEnterprise">
          <el-menu-item index="/enterprise/profile">企业备案信息</el-menu-item>
          <el-menu-item index="/enterprise/reports">数据填报</el-menu-item>
        </template>
        <template v-if="auth.isCity">
          <el-menu-item index="/city/pending">待审核上报</el-menu-item>
        </template>
        <template v-if="auth.isProvince">
          <el-menu-item index="/province/enterprises">备案企业</el-menu-item>
          <el-menu-item index="/province/reports">报表审核</el-menu-item>
          <el-menu-item index="/province/periods">调查期管理</el-menu-item>
          <el-menu-item index="/province/notifications">通知管理</el-menu-item>
          <el-menu-item index="/province/users">用户管理</el-menu-item>
          <el-menu-item index="/province/audit">填报日志审计</el-menu-item>
          <el-menu-item index="/province/metrics">系统监控</el-menu-item>
          <el-menu-item index="/analysis">取样分析</el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>{{ title }}</span>
        <div class="right">
          <span class="who">{{ auth.user?.displayName }}（{{ roleText }}）</span>
          <el-button type="danger" link @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const title = computed(() => route.meta.title || '云南省企业就业失业数据采集系统')

const roleText = computed(() => {
  if (auth.isProvince) return '省'
  if (auth.isCity) return '市'
  if (auth.isEnterprise) return '企业'
  return ''
})

function logout() {
  auth.clear()
  router.push('/login')
}
</script>

<style scoped>
.shell {
  height: 100%;
}
.aside {
  background: #001529;
  color: #fff;
}
.brand {
  padding: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255,255,255,.1);
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}
.main {
  padding: 16px;
}
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.who {
  color: #606266;
  font-size: 14px;
}
</style>
