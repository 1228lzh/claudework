import { reactive } from 'vue'

export const userStore = reactive({
  userId: '',
  fullname: '',
  mobile: '',
  email: '',
  loaded: false,

  get isLoggedIn() {
    return this.loaded && !!this.userId
  },

  setUser(user) {
    this.userId = user.userId || user.user_id || ''
    this.fullname = user.fullname || ''
    this.mobile = user.mobile || ''
    this.email = user.email || ''
    this.loaded = true
  },

  clear() {
    this.userId = ''
    this.fullname = ''
    this.mobile = ''
    this.email = ''
    this.loaded = false
  }
})
