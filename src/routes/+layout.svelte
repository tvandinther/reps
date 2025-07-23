<script lang="ts">
	import '../app.css';

	let { children } = $props();
</script>

<div class="px-4">
	{@render children()}
</div>
