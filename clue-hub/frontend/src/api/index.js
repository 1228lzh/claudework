import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const sid = sessionStorage.getItem('jsessionid')
  if (sid) config.headers['X-JSessionId'] = sid
  return config
})

// ===== 线索相关 =====

/** 提交线索 */
export const submitClue = (data) => api.post('/clue/submit', data)

/** 暂存线索（落库，状态=new） */
export const saveClue = (data) => api.post('/clue/save', data)

/** 获取暂存的线索 */
export const getPending = () => api.get('/clue/pending')

/** 获取线索详情 */
export const getClueDetail = (id) => api.get(`/clue/${id}`)

/** 我的线索列表 */
export const getMyClues = () => api.get('/clue/my')

/** 重新提交线索（退回补充后） */
export const resubmitClue = (id, data) => api.put(`/clue/${id}/resubmit`, data)

/** 上传线索附件 */
export const uploadClueFile = (id, file) => {
  const form = new FormData()
  form.append('file', file)
  return api.post(`/clue/${id}/upload`, form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 获取线索附件列表 */
export const getClueAttachments = (id) => api.get(`/clue/${id}/attachments`)

/** 删除新建状态的线索 */
export const deleteClue = (id) => api.delete(`/clue/${id}`)

// ===== 审核相关 =====

/** 获取审核线索列表 */
export const getReviewClues = (status) => api.get('/review/clues', { params: { status } })

/** 提交审核 */
export const submitReview = (clueId, data) => api.post(`/review/${clueId}`, data)

/** 获取审核历史 */
export const getReviewHistory = (clueId) => api.get(`/review/${clueId}/history`)

/** 上传审核附件 */
export const uploadReviewFile = (reviewRecordId, file) => {
  const form = new FormData()
  form.append('file', file)
  return api.post(`/review/${reviewRecordId}/upload`, form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 获取审核附件列表 */
export const getReviewAttachments = (reviewRecordId) => api.get(`/review/${reviewRecordId}/attachments`)

/** 下载文件URL */
export const getFileUrl = (attachmentId) => `/api/file/${attachmentId}`

export default api
