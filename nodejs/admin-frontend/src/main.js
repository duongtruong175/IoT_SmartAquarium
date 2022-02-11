import Vue from 'vue'
import App from './App.vue'
import './assets/css/tailwind.css'
import i18n from './lang/i18n'
import store from './store/store'
import router from './router/router'
import common from './mixins/common'

Vue.mixin(common);

// Import Loading component, stylesheet
import Loading from 'vue-loading-overlay'
import 'vue-loading-overlay/dist/vue-loading.css'
// global component
Vue.component('loading', Loading);

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  store,
  router,
  i18n,
}).$mount('#app')
