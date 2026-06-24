import request from './request'

export function getUsers(current, size) {
  return request.get('/admin/users', { params: { current, size } })
}

export function updateUserStatus(id, status) {
  return request.put(`/admin/user/${id}/status`, null, { params: { status } })
}

export function addQuestion(data) {
  return request.post('/admin/question', data)
}

export function getQuestions(current, size) {
  return request.get('/admin/questions', { params: { current, size } })
}

export function deleteQuestion(id) {
  return request.delete(`/admin/question/${id}`)
}

export function createPaper(data) {
  return request.post('/admin/paper', data)
}

export function addPaperQuestion(paperId, data) {
  return request.post(`/admin/paper/${paperId}/question`, data)
}

export function extractWords(questionId) {
  return request.post(`/admin/question/${questionId}/extract-words`)
}

export function getOrders(current, size) {
  return request.get('/admin/orders', { params: { current, size } })
}

export function updateOrderStatus(id, status) {
  return request.put(`/admin/order/${id}/status`, null, { params: { status } })
}

export function getPendingCorrections() {
  return request.get('/admin/correction/pending')
}

export function assignCorrection(id, teacherId) {
  return request.put(`/admin/correction/${id}/assign`, null, { params: { teacherId } })
}

export function getDashboard() {
  return request.get('/admin/dashboard')
}
