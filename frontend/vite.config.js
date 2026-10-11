import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss()
  ],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  define: {
    // Bake VITE_API_BASE_URL into the build at compile time (used by api.js)
    __API_BASE_URL__: JSON.stringify(process.env.VITE_API_BASE_URL || '')
  }
})
