<script lang="ts">
	import {exercises, load, save } from '$lib/state.svelte';
	import '../app.css';

	let { children } = $props();
	
	$effect(() => {
		save(exercises)
	})
</script>
<div class="mb-2 bg-white px-4 py-1 rounded-b-sm">
	<a href="/"><h1 class="text-lg font-extrabold">Reps</h1></a>
</div>
<div class="px-4">
	{@render children()}
</div>
