import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig(({ command }) => ({
  base: command === 'serve' ? '' : '/static/dist/',
  publicDir: false,
  build: {
    manifest: true,
    outDir: 'src/main/resources/public/dist',
    assetsDir: 'js',
    rollupOptions: {
      input: 'src/main/javascript/main.js',
    }
  },

  plugins: [vue({
    script: {
      defineModel: true
    }
  })],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src/main/javascript', import.meta.url))
    }
  },

  server: {
    // Needed for changes to picked up when running in WSL on Windows
    watch: {
      usePolling: true
    }
  }
}))