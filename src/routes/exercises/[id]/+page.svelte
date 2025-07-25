<script lang="ts">
	import { goto } from "$app/navigation";
	import { deleteExercise } from "$lib/exercise";
	import { root } from "$lib/routes";
import type { PageProps } from "./$types";

    const { data }: PageProps = $props()
    const exercise = $derived(data.exercise)
</script>

{#if exercise}
<div class="bg-white shadow-md rounded px-8 pt-2 pb-4 mb-4">
    <h3 class="font-bold text-2xl">{exercise.name}</h3>
    <br>
    <p>{exercise.description}</p>
    <div class="flex justify-between">
        <button 
            class="mt-2"
            onclick={() => root.exercises.id(exercise.id).edit.go()}
        >
            Edit
        </button>
        <button 
            class="mt-2 delete"
            onclick={() => {
                deleteExercise(exercise)
                root.go();
            }}
        >
            Delete
        </button>
    </div>
</div>
{:else}
    <span class="dark:text-white">Error: Exercise not found</span>
{/if}
