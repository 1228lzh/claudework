import axios from 'axios'

const authApi = axios.create({
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

/** 获取当前登录用户信息 */
export const getCurrentUser = () => authApi.get('/auth/user')

export default authApi
