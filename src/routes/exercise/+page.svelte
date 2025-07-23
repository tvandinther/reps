<script lang="ts">
	import { page } from "$app/state";
	import AddExercise from "$lib/components/AddExercise.svelte";

    const name = page.url.searchParams.get("name") ?? ""
</script>

<AddExercise {name} />