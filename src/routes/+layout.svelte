<script lang="ts">
	import { goto } from '$app/navigation';
	import {exercises, load, save } from '$lib/state.svelte';
	import '../app.css';

	let { children } = $props();
	
	$effect(() => {
		console.log("Saving exercises")
		save(exercises)
	})
</script>
<header class="mb-2 bg-white px-4 py-1 rounded-b-sm">
	<a href="/"><h1 class="text-2xl font-extrabold">Reps</h1></a>
</header>
<!-- <button
	type="button"
	class="mx-6 my-2"
	onclick={() => window.history.back()}
>
	Back
</button> -->
<div class="px-4">
	{@render children()}
</div>
