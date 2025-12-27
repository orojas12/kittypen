/// <reference types="vitest/config" />
import { defineConfig } from "vite";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  root: resolve(__dirname, "src/test"),
  test: {
    // ... Specify options here.
    alias: {
      "@/": new URL("./src/main/typescript/", import.meta.url).pathname,
    },
  },
});
