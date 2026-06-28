const BASE_URL = '/api'

async function request(url, options = {}) {
  const token = localStorage.getItem('token')
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers
  }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  const response = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers
  })
  const result = await response.json()
  if (result.code !== 1) {
    throw new Error(result.message || '请求失败')
  }
  return result
}

export function get(url, params) {
  let query = ''
  if (params) {
    query = '?' + new URLSearchParams(params).toString()
  }
  return request(url + query, { method: 'GET' })
}

export function post(url, data) {
  return request(url, {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

export function put(url, data) {
  return request(url, {
    method: 'PUT',
    body: JSON.stringify(data)
  })
}

export function del(url) {
  return request(url, { method: 'DELETE' })
}
