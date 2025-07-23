<script>
	import { addExercise } from "$lib";
	import { exercises } from "$lib/state.svelte";

    let searchValue = $state("")
</script>
<h1>Reps</h1>
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
            bind:value={searchValue}
        >
    </div>
    <div class="flex items-center justify-between">
        <button 
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" 
            type="button"
            onclick={() => addExercise(searchValue)}
        >
        Add
      </button>
    </div>
</form>
<div>
    {#each Object.entries(exercises) as [id, exercise] }
        <h3>{exercise.name}</h3>
    {/each}
</div>
