<script lang="ts">
	import { addExercise, search } from "$lib";
	import { exercises } from "$lib/state.svelte";

    let searchValue = $state("")
    let filteredExercises = $derived(search(Object.values(exercises), searchValue, x => x.name))

    $inspect(filteredExercises)
</script>
<h1>Reps</h1>
<form class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
    <div class="flex items-center justify-between">
        <button 
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" 
            type="button"
            onclick={() => addExercise(searchValue)}
        >
        Add {searchValue.length === 0 ? "Exercise" : searchValue}
      </button>
    </div>
    <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="exercise-name">
            Exercise Name
        </label>
        <input 
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
            id="exercise-name" 
            type="text" 
            placeholder="Exercise Name"
            bind:value={searchValue}
        >
    </div>
</form>
<div>
    {#each filteredExercises as exercise }
        <h3>{exercise.name}</h3>
    {/each}
</div>
