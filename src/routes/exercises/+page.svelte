<script lang="ts">
	import ExerciseForm from "$lib/components/ExerciseForm.svelte";
	import { newExercise, type Exercise } from "$lib/exercise";
	import type { PageProps } from "./$types";

	const { data }: PageProps = $props()
	let exercise = $state<Exercise>(newExercise({ name: data.name }))
</script>

<ExerciseForm isNew bind:exercise={exercise} />
