import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue'), meta: { public: true } },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'enterprise/profile', component: () => import('../views/enterprise/Profile.vue') },
      { path: 'enterprise/reports', component: () => import('../views/enterprise/Reports.vue') },
      { path: 'city/pending', component: () => import('../views/city/Pending.vue') },
      { path: 'province/enterprises', component: () => import('../views/province/Enterprises.vue') },
      { path: 'province/reports', component: () => import('../views/province/Reports.vue') },
      { path: 'province/periods', component: () => import('../views/province/SurveyPeriods.vue') },
      { path: 'province/notifications', component: () => import('../views/province/Notifications.vue') },
      { path: 'province/users', component: () => import('../views/province/Users.vue') },
      { path: 'province/audit', component: () => import('../views/province/AuditLogs.vue') },
      { path: 'province/metrics', component: () => import('../views/province/Metrics.vue') },
      { path: 'notifications', component: () => import('../views/Notifications.vue') },
      { path: 'analysis', component: () => import('../views/province/Analysis.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (to.meta.public) return true
  if (!auth.token) return '/login'
  if (!auth.user) {
    try {
      await auth.fetchMe()
    } catch {
      auth.clear()
      return '/login'
    }
  }
  return true
})

export default router
