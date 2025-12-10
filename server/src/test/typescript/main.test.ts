import {expect, test} from "vitest";
import { sum } from "@/sum.ts";

test("main test", () => {
    expect(sum(1, 1) === 2);
})
