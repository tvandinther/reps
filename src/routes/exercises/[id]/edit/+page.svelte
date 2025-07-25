<script lang="ts">
	import ExerciseForm from "$lib/components/ExerciseForm.svelte";
	import type { PageProps } from "./$types";

	const { data }: PageProps = $props()
	let exercise = $state(data.exercise)
</script>

<ExerciseForm bind:exercise={exercise} />
