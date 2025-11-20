# Repository Agent Guidance

- Do **not** include binary or generated assets in outputs or patches. In particular, never surface texture atlases (e.g., `.atlas`, `.pack`) or image assets such as `.png` files in responses or diffs.
- Prefer referencing these assets by path or description instead of embedding their contents.
