import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '../api/http'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const role = computed(() => user.value?.role || null)
  const isProvince = computed(() => role.value === 'PROVINCE')
  const isCity = computed(() => role.value === 'CITY')
  const isEnterprise = computed(() => role.value === 'ENTERPRISE')

  function persist() {
    if (token.value) localStorage.setItem('token', token.value)
    else localStorage.removeItem('token')
    if (user.value) localStorage.setItem('user', JSON.stringify(user.value))
    else localStorage.removeItem('user')
  }

  async function login(username, password) {
    const { data } = await http.post('/auth/login', { username, password })
    token.value = data.token
    user.value = data.user
    persist()
  }

  async function fetchMe() {
    const { data } = await http.get('/auth/me')
    user.value = data
    persist()
  }

  function clear() {
    token.value = ''
    user.value = null
    persist()
  }

  return { token, user, role, isProvince, isCity, isEnterprise, login, fetchMe, clear, persist }
})
