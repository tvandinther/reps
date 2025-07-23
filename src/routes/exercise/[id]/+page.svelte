<script lang="ts">
	import { page } from "$app/state";
	import { exercises, type Id } from "$lib/state.svelte";

    const exercise = exercises[page.params.id as Id]
</script>

{#if exercise}
<div class="bg-white shadow-md rounded px-8 pt-2 pb-4 mb-4">
    <span class="font-bold text-xl">{exercise.name}</span>
    <br>
    <span>{exercise.description}</span>
</div>
{:else}
    <span class="dark:text-white">Error: Exercise not found</span>
{/if}
