import request from './request'

export function getWordList(level, page, size) {
  return request.get('/word/list', { params: { level, page, size } })
}

export function getTodayPlan() {
  return request.get('/word/today-plan')
}

export function updateWordStatus(wordId, status) {
  return request.post(`/word/status/${wordId}`, null, { params: { status } })
}

export function addToVocabulary(wordIds) {
  return request.post('/word/vocabulary/batch', wordIds)
}

export function getMyVocabulary() {
  return request.get('/word/vocabulary/my')
}

export function getWordStats() {
  return request.get('/word/stats')
}
