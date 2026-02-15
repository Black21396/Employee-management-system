export default function FooterComponent() {
  return (
    <footer className="footer mt-auto py-3 bg-dark">
      <div className="container text-center">
        <span className="text-white">
          © {new Date().getFullYear()} Employee Manager. All Rights Reserved.
        </span>
      </div>
    </footer>
  );
}
