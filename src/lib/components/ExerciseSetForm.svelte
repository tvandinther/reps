<script lang="ts">
	import { newExerciseSet } from "$lib/actions";
	import { ExerciseSet } from "$lib/schema";
	import { ExerciseRepsUnit } from "$lib/schema/exercise/reps";


    const { repsUnit, confirm, isNew = false }: { repsUnit: ExerciseRepsUnit, confirm: (exerciseSet: ExerciseSet) => void, isNew?: boolean} = $props()
    const exerciseSet = $state(newExerciseSet({repsUnitType: repsUnit.type}))
</script>

<form class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
    <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="resistance">
            Resistance
        </label>
        <input 
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
            id="resistance" 
            type="number" 
            min="0"
            placeholder="Resistance"
            bind:value={exerciseSet.resistance}
            onfocus={(e) => e.currentTarget.select()}
        >
    </div>
    <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="reps">
            {repsUnit.displayName}
        </label>
        <input 
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
            id="reps" 
            type="number"
            min="0"
            placeholder="Reps"
            bind:value={exerciseSet.reps}
            onfocus={(e) => e.currentTarget.select()}
        >
    </div>
    <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="exertion-rating">
            Exertion Rating
        </label>
        <div class="flex gap-3">
            <input 
                class="border rounded w-full py-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
                id="exertion-rating" 
                type="range"
                min="1"
                max="10" 
                placeholder="Exertion Rating"
                bind:value={exerciseSet.exertionRating}
                onfocus={(e) => e.currentTarget.select()}
            >
            <span class="text-lg">{exerciseSet.exertionRating}</span>
        </div>
    </div>
    <div class="flex items-center justify-between">
        <button 
            class="
            bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded 
            focus:outline-none focus:shadow-outline
            disabled:bg-gray-400
            " 
            type="button"
            onclick={() => confirm(exerciseSet)}
            disabled={false}
        >
        {isNew ? "Add" : "Save"} Note
      </button>
    </div>
</form>