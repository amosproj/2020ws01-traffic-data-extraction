import Vue from 'vue'
import Router from 'vue-router'
import App from '@/App'
import Historization from '@/views/Historization'
import Map from '@/views/Map.vue'

// const Historization = () => import('@/views/Historization')

Vue.use(Router)

export default new Router({
  // mode: 'hash',
  linkActiveClass: 'active',
  // scrollBehavior: () => ({ y: 0 }),
  routes: configRoutes()
})

function configRoutes () {
  return [
    {
      path: '/',
      name: 'App',
      component: App
    },
    {
      path: '/map',
      name: 'Map',
      component: Map
    },
    {
      path: '/historization',
      name: 'Historization',
      component: Historization
    },

  ]
}
