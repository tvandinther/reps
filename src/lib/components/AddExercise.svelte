<script lang="ts">
	import { goto, pushState } from "$app/navigation";
	import { addExercise } from "$lib";
	import { exercises } from "$lib/state.svelte";

    const { name = "" }: {name?: string} = $props()

    let exerciseData = $state({
        name: name,
        description: "",
    })
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
            bind:value={exerciseData.name}
        >
    </div>
    <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="exercise-name">
            Description
        </label>
        <input 
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
            id="exercise-name" 
            type="text" 
            placeholder="Description"
            bind:value={exerciseData.description}
        >
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
                const id = addExercise(exercises, exerciseData)
                if (id === null) goto("/")
                else goto("/exercise/" + id)
            }}
            disabled={exerciseData.name.trim().length === 0}
        >
        Add {exerciseData.name}
      </button>
    </div>
</form>