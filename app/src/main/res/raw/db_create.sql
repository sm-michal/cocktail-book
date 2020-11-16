create table ingredient_types (
    id integer primary key,
    name text
);

create table ingredients (
    id integer primary key autoincrement,
    type integer,
    name text,
    foreign key(type) references ingredient_types(id)
);

create table recipes (
    id integer primary key autoincrement,
    name text,
    description text,
    glass_type text
);

create table recipes_ingredients (
    recipe_id integer,
    ingredient_id integer,
    quantity number,
    unit text,
    foreign key(recipe_id) references recipes(id),
    foreign key(ingredient_id) references ingredients(id)
);

create table ingredients_in_storage (
    ingredient_id integer,
    foreign key(ingredient_id) references ingredients(id)
);

