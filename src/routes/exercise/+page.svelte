<script lang="ts">
	import { page } from "$app/state";
	import AddExercise from "$lib/components/AddExercise.svelte";
	import type { PageProps } from "./$types";

    const name = page.url.searchParams.get("name") ?? ""
	const { data }: PageProps = $props()
</script>

<AddExercise name={data.name} />