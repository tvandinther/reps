export function search(values: any[], searchString: string): any[];
export function search<T>(values: T[], searchString: string, getter: (x: T) => string): T[];
export function search<T>(
	values: T[],
	searchString: string,
	getter?: (x: T) => string
): T[] | any[] {
	const actualGetter: (x: T) => string = getter || ((x: any) => String(x));
	const results = values.filter((item) => actualGetter(item).includes(searchString));

	return results as T[] | any[];
}
