<script lang="ts">
	import { goto } from "$app/navigation";
	import { search } from "$lib";
	import ExerciseItem from "$lib/components/ExerciseItem.svelte";
	import { exercises } from "$lib/state.svelte";

    let searchValue = $state("")
    let filteredExercises = $derived(search(Object.values(exercises), searchValue, x => x.name)) 

    $inspect(filteredExercises)
</script>
<form class="bg-white shadow-md rounded px-8 pt-4 pb-1 mb-4">
    <div class="flex items-center justify-between">
        <button 
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" 
            type="button"
            onclick={() => goto(`/exercise?name=${searchValue}`)}
        >
        Add {searchValue.length === 0 ? "Exercise" : searchValue}
      </button>
    </div>
    <div class="mb-4">
        <input 
            class="shadow mt-2 appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
            id="exercise-name" 
            type="text" 
            placeholder="Exercise Name"
            bind:value={searchValue}
        >
    </div>
</form>
<hr class="h-px my-8 bg-gray-200 border-0 dark:bg-gray-700">
<div class="flex flex-col gap-2">
    {#each filteredExercises as exercise }
        <ExerciseItem {exercise} />
    {/each}
</div>
