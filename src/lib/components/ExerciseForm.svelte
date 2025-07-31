<script lang="ts">
	import { ExerciseRepsUnits, persistExercise, type Exercise } from "$lib/exercise";
	import { root } from "$lib/routes";

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
            rows="8"
            bind:value={exercise.description}
        ></textarea>
    </div>
    <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="exercise-reps-unit">
            Reps Unit
        </label>
        <select 
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
            id="exercise-reps-unit" 
            placeholder="Description"
            bind:value={exercise.repsUnit}
        >
            {#each ExerciseRepsUnits as unit (unit.type)}
                <option value={unit.type}>{unit.displayName}</option>
            {/each}
        </select>
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
                root.exercises.id(exercise.id).go()
            }}
            disabled={exercise.name.trim().length === 0}
        >
        {isNew ? "Add" : "Save"} {exercise.name}
      </button>
    </div>
</form>