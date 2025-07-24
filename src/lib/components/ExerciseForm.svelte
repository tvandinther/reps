<script lang="ts">
	import { goto } from "$app/navigation";
	import { persistExercise, type Exercise } from "$lib/exercise";

    const { exercise = $bindable(), isNew = false }: {exercise: Exercise, isNew?: boolean} = $props()
</script>

<form class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
    <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="exercise-name">
            Exercise Name
        </label>
        <input 
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
            id="exercise-name" 
            type="text" 
            placeholder="Exercise Name"
            bind:value={exercise.name}
        >
    </div>
    <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="exercise-name">
            Description
        </label>
        <textarea 
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
            id="exercise-name" 
            placeholder="Description"
            bind:value={exercise.description}
        ></textarea>
    </div>
    <div class="flex items-center justify-between">
        <button 
            class="
            bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded 
            focus:outline-none focus:shadow-outline
            disabled:bg-gray-400
            " 
            type="button"
            onclick={() => {
                persistExercise(exercise)
                goto("/exercise/" + exercise.id)
            }}
            disabled={exercise.name.trim().length === 0}
        >
        {isNew ? "Add" : "Save"} {exercise.name}
      </button>
    </div>
</form>