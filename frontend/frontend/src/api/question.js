import request from './request'

export function getReadingQuestions() {
  return request.get('/question/reading')
}

export function getListeningQuestions() {
  return request.get('/question/listening')
}

export function getPapers(level) {
  return request.get('/question/papers', { params: { level } })
}

export function getPaperDetail(paperId) {
  return request.get(`/question/paper/${paperId}`)
}

export function submitAnswers(data) {
  return request.post('/question/submit', data)
}

export function getWrongQuestions() {
  return request.get('/question/wrong')
}

export function getQuestionWords(questionId) {
  return request.get(`/question/${questionId}/words`)
}
