import request from './request'

export function addScore(data) {
  return request.post('/score/add', data)
}

export function getMyScores() {
  return request.get('/score/my')
}

export function getScoreTrend() {
  return request.get('/score/trend')
}
