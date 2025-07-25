<script lang="ts">
	import { ExerciseNote, newExerciseNote } from "$lib/exercise";

    const { confirm, isNew = false }: { confirm: (exerciseNote: ExerciseNote) => void, isNew?: boolean} = $props()
    const exerciseNote = $state(newExerciseNote())
</script>

<form class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
    <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="exercise-name">
            Note
        </label>
        <textarea 
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
            id="exercise-name" 
            rows="8"
            placeholder="Note"
            bind:value={exerciseNote.content}
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
            onclick={() => confirm(exerciseNote)}
            disabled={exerciseNote.content.trim().length === 0}
        >
        {isNew ? "Add" : "Save"} Note
      </button>
    </div>
</form>