<script lang="ts">
	import { newExercise } from "$lib/actions";
	import ExerciseForm from "$lib/components/ExerciseForm.svelte";
	import { Exercise } from "$lib/schema";
	import type { PageProps } from "./$types";

	const { data }: PageProps = $props()
	let exercise = $state<Exercise>(newExercise({ name: data.name }))
</script>

<ExerciseForm isNew bind:exercise={exercise} />
