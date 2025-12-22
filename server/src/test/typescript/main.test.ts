import {expect, test} from "vitest";
import { sum } from "@/sum";

test("main test", () => {
    expect(sum(1, 1) === 2);
})
