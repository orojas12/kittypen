/// <reference types="vitest/config" />
import { defineConfig } from 'vite'
import { resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  root: resolve(__dirname, "src/main"),
  build: {
    rollupOptions: {
      input: {
        index: resolve(__dirname, "src/main/pages/index.html"),
      },
    },
    outDir: resolve(__dirname, "src/main/resources"),
    emptyOutDir: false
  },
  test: {
    // ... Specify options here.
    alias: {
        "@/": new URL("./src/main/typescript/", import.meta.url).pathname,
    }
  },
})