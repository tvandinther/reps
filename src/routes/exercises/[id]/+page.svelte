<script lang="ts">
	import ExerciseView from "$lib/components/ExerciseView.svelte";
	import type { PageProps } from "./$types";

    const { data }: PageProps = $props()
    const exercise = $derived(data.exercise)
</script>

{#if exercise}
    <ExerciseView {exercise} />
{:else}
    <span class="dark:text-white">Error: Exercise not found</span>
{/if}
