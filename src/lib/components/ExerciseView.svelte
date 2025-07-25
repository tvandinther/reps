<script lang="ts">
	import ExerciseEvents from "$lib/components/ExerciseEvents.svelte";
	import ExerciseNoteForm from "$lib/components/ExerciseNoteForm.svelte";
	import { deleteExercise, Exercise, newExerciseNote, persistExerciseNote } from "$lib/exercise";
	import { root } from "$lib/routes";

    const { exercise }: {exercise: Exercise} = $props()
    const events = $derived(Object.values(exercise.events))

    let showExerciseNoteForm = $state(false)
    let exerciseNote = $state(newExerciseNote())
</script>

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
<hr class="h-px my-8 bg-gray-200 border-0 dark:bg-gray-700">
<div class="bg-white shadow-md rounded px-8 pt-2 pb-4 mb-4">
    <button>
        Log Set
    </button>
    <button onclick={() => showExerciseNoteForm = true}>
        Add Note
    </button>
    {#if showExerciseNoteForm}
        <ExerciseNoteForm bind:exerciseNote={exerciseNote} 
            confirm={() => {
                persistExerciseNote(exercise, exerciseNote)
                showExerciseNoteForm = false
                exerciseNote = newExerciseNote()
            }}
        />
    {/if}
    <div class="flex flex-col gap-1 py-2">
        <ExerciseEvents {events} />
    </div>
</div>
