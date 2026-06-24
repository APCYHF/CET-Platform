import request from './request'

export function submitComposition(data) {
  return request.post('/composition/submit', data)
}

export function getMyCompositions() {
  return request.get('/composition/my')
}
