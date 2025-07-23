<script lang="ts">
	import '../app.css';

	let { children } = $props();
</script>
<div class="mb-2 bg-white px-4 py-1 rounded-b-sm">
	<a href="/"><h1 class="text-lg font-extrabold">Reps</h1></a>
</div>
<div class="px-4">
	{@render children()}
</div>
