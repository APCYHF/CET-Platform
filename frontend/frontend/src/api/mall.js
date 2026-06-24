import request from './request'

export function getProducts() {
  return request.get('/mall/products')
}

export function createOrder(productId, payType) {
  return request.post('/mall/order', null, { params: { productId, payType } })
}

export function getMyOrders() {
  return request.get('/mall/orders')
}
