import axios from 'axios'

const authApi = axios.create({
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

authApi.interceptors.request.use(config => {
  const sid = sessionStorage.getItem('jsessionid')
  if (sid) config.headers['X-JSessionId'] = sid
  return config
})

/** 获取当前登录用户信息 */
export const getCurrentUser = () => authApi.get('/auth/user')

export default authApi
